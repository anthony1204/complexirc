package top.anthonycat.complexirc.client;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine
//this is vortextraveler's code not mine

public class NOT_MY_CODE {

   private static final String[] HAND_PICKED = {
           "mreow", "mow", "meow", "miow", "mer", "mrrr", "mrrp", "rmow",
           "meeew", "meooowww", "mew", "mrr", "meeow", "miyow", "myeow", "miw",
           "meoww", "mrrw", "mrrow", "meoooww", "meew", "miiooww", "mrreow", "mrow",
           "mroew", "mieow", "mrieow", "mrrrp", "miowww", "mrreoowww", ":3c", ":3",
           "mrp", "mraw", "mriw", "maow", "maw", ">//<", ">=<", "owo", "uwu", "awa", ">:3"
   };
   private static final String[] ONSETS = {"m", "mr", "mrr", "ny", "mw"};
   private static final String[] VOWELS = {"e", "i", "a", "o", "ee", "ia", "ya", "eo"};
   private static final String[] ENDINGS = {
           "w", "ow", "r", "rp", "u", "uw", "ww", "wr", "rr", "aw", "iw", "rw"
   };
   private static final java.util.Set<String> BANNED = java.util.Set.of(
           "mir", "mar", "mau", "mor", "mou"
   );
   private static final String[] SOUNDS = buildSounds();
   private static final BigInteger BASE = BigInteger.valueOf(SOUNDS.length);
   private static String[] buildSounds() {
      java.util.LinkedHashSet<String> set = new java.util.LinkedHashSet<>(java.util.Arrays.asList(HAND_PICKED));
      for (String o : ONSETS)
         for (String v : VOWELS)
            for (String e : ENDINGS) {
               if ((o.equals("ny") || o.equals("mw")) && v.startsWith("y")) continue; // "nyya", "mwya"
               String w = o + v + e;
               if (!BANNED.contains(w)) set.add(w);
            }
      return set.toArray(new String[0]);
   }

   private static final Map<String, Integer> SOUND_INDEX = new HashMap<>();
   static {
      for (int i = 0; i < SOUNDS.length; i++) SOUND_INDEX.put(SOUNDS[i], i);
   }

   private static final int NONCE_LEN = 4;
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
      try {
         byte[] in = fromCatSounds(catText);
//      if (in.length < NONCE_LEN) throw new IllegalArgumentException("Not enough meows");

         byte[] nonce = new byte[NONCE_LEN];
         System.arraycopy(in, 0, nonce, 0, NONCE_LEN);
         byte[] cipher = new byte[in.length - NONCE_LEN];
         System.arraycopy(in, NONCE_LEN, cipher, 0, cipher.length);
         return new String(xorWithKeystream(key, nonce, cipher), StandardCharsets.UTF_8);
      } catch (Exception e) {
         return "failed to decode, original: "+catText;
      }
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

   // A 0x01 marker byte goes in front so leading zero bytes survive the number conversion.
   private static String toCatSounds(byte[] bytes) {
      byte[] marked = new byte[bytes.length + 1];
      marked[0] = 1;
      System.arraycopy(bytes, 0, marked, 1, bytes.length);
      BigInteger n = new BigInteger(1, marked);

      StringBuilder sb = new StringBuilder();
      while (n.signum() > 0) {
         BigInteger[] qr = n.divideAndRemainder(BASE);
         sb.insert(0, sb.length() > 0 ? SOUNDS[qr[1].intValue()] + " " : SOUNDS[qr[1].intValue()]);
         n = qr[0];
      }
      return sb.toString();
   }

   private static byte[] fromCatSounds(String catText) {
      String trimmed = catText.trim();
      if (trimmed.isEmpty()) return new byte[0];

      BigInteger n = BigInteger.ZERO;
      for (String word : trimmed.split("\\s+")) {
         n = n.multiply(BASE).add(BigInteger.valueOf(soundValue(word)));
      }
      byte[] raw = n.toByteArray();
      int start = raw[0] == 0 ? 1 : 0; // skip BigInteger's sign byte
      if (raw.length <= start || raw[start] != 1) throw new IllegalArgumentException("Not a cat message");
      byte[] out = new byte[raw.length - start - 1];
      System.arraycopy(raw, start + 1, out, 0, out.length);
      return out;
   }

   private static int soundValue(String word) {
      Integer v = SOUND_INDEX.get(word.toLowerCase());
      if (v == null) throw new IllegalArgumentException("Unknown cat sound: " + word);
      return v;
   }
}
