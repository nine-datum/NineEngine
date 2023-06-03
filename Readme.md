First of all -- not recommended to use java.nio buffers. Better to use simple arrays instead.
Use of a java.nio buffer may cause JVM access violation exception.