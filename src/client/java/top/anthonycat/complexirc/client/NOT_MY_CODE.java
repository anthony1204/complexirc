package top.anthonycat.complexirc.client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!
//THIS IS NOT MY CODE!!!!!

//credit to vortextraveler for this code
public class NOT_MY_CODE {



   public static String fulldecode(String k, String todecode){
      int i = todecode.indexOf("C:");
      String full;
      full = todecode;
      if (i!=-1) full = todecode.substring(i);
      return decrypt(k,full);
   }

   //begin not my code section

   private static final String[] SOUNDS = {
           "mreow", "mow", "meow", "miow", "mer", "mrrr", "mrrp", "rmow",
           "meeew", "meooowww", "mew", "mrr", "meeow", "miyow", "myeow", "miw",
           "meoww", "mrrw", "mrrow", "meoooww", "meew", "miiooww", "mrreow", "mrow",
           "mroew", "mieow", "mrieow", "mrrrp", "miowww", "mrreoowww", ":3c", ":3"
   };
   private static final int BITS = 5; // 32 sounds = 5 bits each
   private static final Map<String, Integer> SOUND_INDEX = new HashMap<>();
   static {
      for (int i = 0; i < SOUNDS.length; i++) SOUND_INDEX.put(SOUNDS[i], i);
   }

   private static final int NONCE_LEN = 8;
   private static final SecureRandom RANDOM = new SecureRandom();

   public static String encrypt(String key, String text) {
      byte[] nonce = new byte[NONCE_LEN];
      RANDOM.nextBytes(nonce);
      byte[] data = text.getBytes(StandardCharsets.UTF_8);
      byte[] cipher = xorWithKeystream(key, nonce, data);

      byte[] out = new byte[NONCE_LEN + cipher.length];
      System.arraycopy(nonce, 0, out, 0, NONCE_LEN);
      System.arraycopy(cipher, 0, out, NONCE_LEN, cipher.length);
      return toCatSounds(out);
   }

   public static String decrypt(String key, String catText) {
      byte[] in = fromCatSounds(catText);
//      if (in.length < NONCE_LEN) throw new IllegalArgumentException("Not enough meows");

      byte[] nonce = new byte[NONCE_LEN];
      System.arraycopy(in, 0, nonce, 0, NONCE_LEN);
      byte[] cipher = new byte[in.length - NONCE_LEN];
      System.arraycopy(in, NONCE_LEN, cipher, 0, cipher.length);
      return new String(xorWithKeystream(key, nonce, cipher), StandardCharsets.UTF_8);
   }

   // XOR is its own inverse, so this both encrypts and decrypts.
   private static byte[] xorWithKeystream(String key, byte[] nonce, byte[] data) {
      MessageDigest sha;
      try {
         sha = MessageDigest.getInstance("SHA-256");
      } catch (NoSuchAlgorithmException e) {
         throw new IllegalStateException(e);
      }
      byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
      byte[] out = new byte[data.length];
      byte[] block = new byte[0];
      int counter = 0;
      for (int i = 0; i < data.length; i++) {
         if (i % 32 == 0) {
            sha.reset();
            sha.update(keyBytes);
            sha.update(nonce);
            sha.update(new byte[] {
                    (byte) (counter >>> 24), (byte) (counter >>> 16),
                    (byte) (counter >>> 8), (byte) counter
            });
            block = sha.digest();
            counter++;
         }
         out[i] = (byte) (data[i] ^ block[i % 32]);
      }
      return out;
   }

   private static String toCatSounds(byte[] bytes) {
      StringBuilder sb = new StringBuilder();
      int buffer = 0, bits = 0;
      for (byte b : bytes) {
         buffer = (buffer << 8) | (b & 0xFF);
         bits += 8;
         while (bits >= BITS) {
            bits -= BITS;
            appendSound(sb, (buffer >> bits) & 31);
         }
      }
      if (bits > 0) appendSound(sb, (buffer << (BITS - bits)) & 31); // pad last sound with zeros
      return sb.toString();
   }

   private static void appendSound(StringBuilder sb, int value) {
      if (sb.length() > 0) sb.append(' ');
      sb.append(SOUNDS[value]);
   }

   private static byte[] fromCatSounds(String catText) {
      String trimmed = catText.trim();
      if (trimmed.isEmpty()) return new byte[0];
      String[] words = trimmed.split("\\s+");

      byte[] out = new byte[words.length * BITS / 8];
      int buffer = 0, bits = 0, pos = 0;
      for (String word : words) {
         buffer = (buffer << BITS) | soundValue(word);
         bits += BITS;
         if (bits >= 8) {
            bits -= 8;
            if (pos < out.length) out[pos++] = (byte) (buffer >> bits);
         }
      }
      return out;
   }

   private static int soundValue(String word) {
      Integer v = SOUND_INDEX.get(word.toLowerCase());
      if (v == null) return 1;
      return v;
   }
}
