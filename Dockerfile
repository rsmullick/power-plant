FROM ubuntu:latest
LABEL authors="shahr"

ENTRYPOINT ["top", "-b"]