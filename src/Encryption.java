import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class Encryption {
    static final String SEP =  File.separator;

    private File file;
    private String password;

    Encryption(File file, String password) {
        this.file = file;
        this.password = password;
    }

    public boolean encrypt() throws Exception {
        if (!file.exists() || password == null || file == null) {
            return false;
        }

        byte[] salt = new byte[8];
        SecureRandom srand = new SecureRandom();
        srand.nextBytes(salt);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, genKey(password.toCharArray(), salt));
        AlgorithmParameters params = cipher.getParameters();

        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

        FileInputStream in = new FileInputStream(file);
        FileOutputStream out = preOutput(in, file.getParent() +
                SEP + file.getName() + ".tmp", cipher);
        byte[] output = cipher.doFinal();
        if (output != null) {
            out.write(output);
        }

        in.close();
        out.flush();
        out.write(salt);
        out.write(iv);
        out.close();

        File temp = new File(file.getParent() + SEP + file.getName() + ".tmp");
        file.delete();
        return temp.renameTo(file);
    }

    public boolean decrypt() throws Exception {
        if (!file.exists() || password == null || file == null) {
            return false;
        }

        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        byte[] salt = new byte[8];
        byte[] iv = new byte[16];
        raf.seek(file.length() - (salt.length + iv.length));
        raf.read(salt, 0, salt.length);
        raf.seek(file.length() - (iv.length));
        raf.read(iv, 0, iv.length);
        raf.setLength(file.length() - (salt.length + iv.length));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, genKey(password.toCharArray(), salt), new IvParameterSpec(iv));

        FileInputStream in = new FileInputStream(file);
        FileOutputStream out = preOutput(in, file.getParent() +
                SEP + file.getName() + ".tmp", cipher);

        try {
            byte[] output = cipher.doFinal();
            if (output != null) {
                out.write(output);
            }
        } catch (Exception e) {
            raf.seek(file.length());
            raf.write(salt);
            raf.seek(file.length());
            raf.write(iv);
            raf.close();
            in.close();
            out.close();
            File temp = new File(file.getParent() + SEP + file.getName() + ".tmp");
            temp.delete();
            return false;
        }

        raf.close();
        in.close();
        out.flush();
        out.close();

        File temp = new File(file.getParent() + SEP + file.getName() + ".tmp");
        file.delete();
        temp.renameTo(file);
        return true;
    }

    private SecretKey genKey(char[] password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(password, salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(keySpec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    private FileOutputStream preOutput(FileInputStream in, String path, Cipher cipher) throws Exception {
        FileOutputStream out = new FileOutputStream(path);
        byte[] b = new byte[64];
        int read;
        while ((read = in.read(b)) != -1) {
            byte[] output = cipher.update(b, 0, read);
            if (output != null) {
                out.write(output);
            }
        }
        return out;
    }
}
