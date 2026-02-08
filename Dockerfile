# high-performance, production-ready OpenJDK distribution
ARG BUILD_JDK_IMAGE=eclipse-temurin:21-jdk-jammy
ARG RUNTIME_IMAGE=eclipse-temurin:21-jre-jammy

FROM ${BUILD_JDK_IMAGE} AS builder

# Always set servers to UTC time to avoid timezone confusion in logs.
ENV TZ=UTC

# If we remove these lines, especially in a minimal image like Alpine, the OS usually defaults to the C locale (ASCII only).
# This creates three risks:
# - Java Strings: Java reads the OS default encoding on startup (file.encoding). If the OS reports ASCII, Java might corrupt data when reading a UTF-8 text file.
# - Filenames: If the app tries to save a file named report_résumé.pdf, the accent (é) might cause an 'Invalid Path' error.
# - Logs: Logs containing non-English characters might show up as question marks (??) or garbled text, making debugging impossible."
ENV LANG=C.UTF-8
# aggressively prevent any libraries or tools from accidentally falling back to ASCII
ENV LC_ALL=C.UTF-8

# Tells Gradle where to store downloaded libraries.
ENV GRADLE_USER_HOME=/cache/.gradle


# In Linux, IDs 0-999 are usually reserved for the System/Root. 1000 is conventionally the first "normal" human user created on a system.
# By hardcoding it to 1000, we ensure consistency. If we mount a volume from our laptop (where we are likely user 1000) into the container, the permissions will match, and we won't get "Permission Denied" errors.

# -s /bin/sh: Sets the default Shell.
# This ensures that if we ever need to "exec" into the container to debug, we have a working terminal.
RUN groupadd --gid 1000 gradle && \
    useradd --uid 1000 --gid gradle --shell /bin/sh --create-home gradle
# it is safer to run processes as a non-root user. If a build script were malicious or buggy, it minimizes the damage it can do to the container's file system.
# by creating a user gradle (UID 1000) explicitly, we ensure all files generated during the build are owned by a predictable non-root user.

WORKDIR /workspace

# Layering optimization: first copy files that change less frequently.
COPY --chown=gradle:gradle gradle ./gradle
COPY --chown=gradle:gradle gradlew ./
COPY --chown=gradle:gradle settings.gradle.kts ./
COPY --chown=gradle:gradle gradle.properties ./
COPY --chown=gradle:gradle build.gradle.kts ./
RUN chmod +x ./gradlew

COPY --chown=gradle:gradle backend ./backend

# --mount=...: BuildKit feature, attach an extra filesystem mount only while this RUN executes
# `type=cache,target=/cache/.gradle` tells Docker: "Save the contents of /cache/.gradle between builds on my local machine/CI."
# This prevents Gradle from re-downloading the deps every time.
# --no-daemon: Gradle usually keeps a background process running to be fast. In Docker, we want it to die immediately after the command to save memory.
RUN --mount=type=cache,target=/cache/.gradle \
    ./gradlew :backend:onboard-core:classes --no-daemon -x test

RUN --mount=type=cache,target=/cache/.gradle \
    ./gradlew :backend:onboard-loan-origination:classes --no-daemon -x test

RUN --mount=type=cache,target=/cache/.gradle \
    ./gradlew :backend:onboard-registration:classes --no-daemon -x test

RUN --mount=type=cache,target=/cache/.gradle \
    ./gradlew :backend:onboard-provider:classes --no-daemon -x test

RUN --mount=type=cache,target=/cache/.gradle \
    ./gradlew :backend:onboard-provider:bootJar --no-daemon

FROM ${RUNTIME_IMAGE} AS runtime

WORKDIR /app

RUN groupadd --gid 1001 appgroup && \
    useradd --uid 1001 --gid appgroup --shell /bin/sh --create-home appuser

COPY --chown=appuser:appgroup --from=builder /workspace/backend/onboard-provider/build/libs/app.jar /app/boot.jar

USER appuser

ENV JAVA_TOOL_OPTIONS="\
    -XX:InitialRAMPercentage=70.0 \
    -XX:MaxRAMPercentage=70.0 \
    -XX:+ExitOnOutOfMemoryError \
    -Dfile.encoding=UTF-8 \
    -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080/tcp 8081/tcp

STOPSIGNAL SIGTERM

ENTRYPOINT ["java", "-jar", "/app/boot.jar"]
