cd src
javac Main.java Encryption.java
jar cfve FileEncryptor.jar Main Main.class Encryption.class
mv FileEncryptor.jar ../
rm Main.class Encryption.class
