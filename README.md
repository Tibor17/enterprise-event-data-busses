# Motivation

Using JMS with two busses:

1. Enterprise Event Bus
2. Enterprise Data Bus

We are sending events via EEB which sends message without data.
It only has event attributes as well as `java.util.Map`.

Sending data via EDB with name for particular data and data (XML, JSON) itself.

