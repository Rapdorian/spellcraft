FROM fedora

# Instal deps
RUN dnf install -y java-latest-openjdk-devel.x86_64 git unzip
RUN curl --location --show-error -O --url https://services.gradle.org/distributions/gradle-8.9-bin.zip
RUN mkdir -p /opt/gradle
RUN unzip -d /opt/gradle gradle-8.9-bin.zip
ENV PATH="$PATH:/opt/gradle/gradle-8.9/bin"