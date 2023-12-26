# GraalJS Thread Pool Demo

This repo contains demo code of multi-threading in GraalJS.

Core concept is that **you can't access JavaScript context in multi-threading environment**.

JavaScript is designed to be single-threaded, and GraalJS just follows the design.

You CAN create thread pool in JavaScript context, but you CAN'T submit any task to it directly from JavaScript context.

If you want to execute some tasks in multiple threads, you have to wrap the code into a Java method and call it from JavaScript.
