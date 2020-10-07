/*
 * This file is part of Security Shepherd.
 * 
 * Security Shepherd is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Security Shepherd.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.owasp.securityshepherd.test.util;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.owasp.securityshepherd.authentication.PasswordAuthRepository;
import org.owasp.securityshepherd.authentication.UserAuthRepository;
import org.owasp.securityshepherd.configuration.ConfigurationRepository;
import org.owasp.securityshepherd.module.ModulePointRepository;
import org.owasp.securityshepherd.module.ModuleRepository;
import org.owasp.securityshepherd.module.csrf.CsrfAttackRepository;
import org.owasp.securityshepherd.scoring.CorrectionRepository;
import org.owasp.securityshepherd.scoring.SubmissionRepository;
import org.owasp.securityshepherd.user.ClassRepository;
import org.owasp.securityshepherd.user.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public final class TestUtils {

  public static final long[] INVALID_IDS = {-1L, -1000L, 0L, -1234567L, -500L};

  public static final boolean INITIAL_BOOLEAN = false;

  public static final boolean[] BOOLEANS = {false, true};

  public static final Boolean[] BOOLEANS_WITH_NULL = {null, false, true};

  public static final Long INITIAL_LONG = 0L;

  public static final Long[] LONGS = {
    INITIAL_LONG,
    1L,
    -1L,
    100L,
    -100L,
    1000L,
    -1000L,
    Long.valueOf(Integer.MAX_VALUE) + 1,
    Long.valueOf(Integer.MIN_VALUE - 1),
    123456789L,
    -12346789L,
    Long.MAX_VALUE,
    Long.MIN_VALUE
  };

  public static final Long[] LONGS_WITH_NULL = (Long[]) ArrayUtils.addAll(LONGS, (Long) null);

  public static final LocalDateTime INITIAL_LOCALDATETIME = LocalDateTime.MIN;

  public static final LocalDateTime[] LOCALDATETIMES = {
    INITIAL_LOCALDATETIME,
    INITIAL_LOCALDATETIME.plusNanos(1),
    INITIAL_LOCALDATETIME.plusSeconds(1),
    INITIAL_LOCALDATETIME.plusMinutes(1),
    INITIAL_LOCALDATETIME.plusHours(1),
    INITIAL_LOCALDATETIME.plusDays(1),
    INITIAL_LOCALDATETIME.plusWeeks(1),
    INITIAL_LOCALDATETIME.plusMonths(1),
    INITIAL_LOCALDATETIME.plusYears(1),
    INITIAL_LOCALDATETIME.plusYears(1000),
    INITIAL_LOCALDATETIME.plusYears(100000),
    LocalDateTime.MAX
  };

  public static final LocalDateTime[] LOCALDATETIMES_WITH_NULL =
      (LocalDateTime[]) ArrayUtils.addAll(LOCALDATETIMES, (LocalDateTime) null);

  public static final String INITIAL_STRING = "";

  public static final String[] STRINGS = {
    INITIAL_STRING,
    "Test String",
    "åäö",
    "me@example.com",
    "1;DROP TABLE users",
    " ",
    "%",
    "_",
    "-",
    "--",
    "జ్ఞ‌ా",
    "Ｔｈｅ ｑｕｉｃｋ ｂｒｏｗｎ ｆｏｘ ｊｕｍｐｓ ｏｖｅｒ ｔｈｅ ｌａｚｙ ｄｏｇ",
    "𝐓𝐡𝐞 𝐪𝐮𝐢𝐜𝐤 𝐛𝐫𝐨𝐰𝐧 𝐟𝐨𝐱 𝐣𝐮𝐦𝐩𝐬 𝐨𝐯𝐞𝐫 𝐭𝐡𝐞 𝐥𝐚𝐳𝐲 𝐝𝐨𝐠",
    "𝕿𝖍𝖊 𝖖𝖚𝖎𝖈𝖐 𝖇𝖗𝖔𝖜𝖓 𝖋𝖔𝖝 𝖏𝖚𝖒𝖕𝖘 𝖔𝖛𝖊𝖗 𝖙𝖍𝖊 𝖑𝖆𝖟𝖞 𝖉𝖔𝖌",
    "𝑻𝒉𝒆 𝒒𝒖𝒊𝒄𝒌 𝒃𝒓𝒐𝒘𝒏 𝒇𝒐𝒙 𝒋𝒖𝒎𝒑𝒔 𝒐𝒗𝒆𝒓 𝒕𝒉𝒆 𝒍𝒂𝒛𝒚 𝒅𝒐𝒈",
    "𝓣𝓱𝓮 𝓺𝓾𝓲𝓬𝓴 𝓫𝓻𝓸𝔀𝓷 𝓯𝓸𝔁 𝓳𝓾𝓶𝓹𝓼 𝓸𝓿𝓮𝓻 𝓽𝓱𝓮 𝓵𝓪𝔃𝔂 𝓭𝓸𝓰",
    "𝕋𝕙𝕖 𝕢𝕦𝕚𝕔𝕜 𝕓𝕣𝕠𝕨𝕟 𝕗𝕠𝕩 𝕛𝕦𝕞𝕡𝕤 𝕠𝕧𝕖𝕣 𝕥𝕙𝕖 𝕝𝕒𝕫𝕪 𝕕𝕠𝕘",
    "𝚃𝚑𝚎 𝚚𝚞𝚒𝚌𝚔 𝚋𝚛𝚘𝚠𝚗 𝚏𝚘𝚡 𝚓𝚞𝚖𝚙𝚜 𝚘𝚟𝚎𝚛 𝚝𝚑𝚎 𝚕𝚊𝚣𝚢 𝚍𝚘𝚐",
    "😍",
    "👩🏽",
    "👾 🙇 💁 🙅 🙆 🙋 🙎 🙍",
    "🐵 🙈 🙉 🙊",
    "❤️ 💔 💌 💕 💞 💓 💗 💖 💘 💝 💟 💜 💛 💚 💙",
    "✋🏿 💪🏿 👐🏿 🙌🏿 👏🏿 🙏🏿",
    "🚾 🆒 🆓 🆕 🆖 🆗 🆙 🏧",
    "0️⃣ 1️⃣ 2️⃣ 3️⃣ 4️⃣ 5️⃣ 6️⃣ 7️⃣ 8️⃣ 9️⃣ 🔟",
    "ثم نفس سقطت وبالتحديد،, جزيرتي باستخدام أن دنو. إذ هنا؟ الستار وتنصيب كان. أهّل ايطاليا، بريطانيا-فرنسا قد أخذ. سليمان، إتفاقية بين ما, يذكر الحدود أي بعد, معاملة بولندا، الإطلاق عل إيو.",
    "בְּרֵאשִׁי  בָּרָא אֱלֹהִים, אֵת הַשָּׁמַיִם, וְאֵת        הָיְתָהtest",
    " ﷽ ",
    "ﷺ",
    "مُنَاقَشَةُ سُبُلِ اِسْتِخْدَامِ اللُّغَةِ فِي النُّظُمِ الْقَائِمَةِ وَفِيم يَخُصَّ التَّطْبِيقَاتُ الْحاسُوبِيَّةُ، "
  };

  public static final String INITIAL_NAME = "id";

  public static final String[] NAMES = {INITIAL_NAME, "id-with-hyphen", "abc123"};

  public static final String[] INVALID_NAMES = {"", null};

  public static final String[] STRINGS_WITH_NULL =
      (String[]) ArrayUtils.addAll(STRINGS, (String) null);

  public static final byte[] INITIAL_BYTE_ARRAY = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

  public static final byte[][] BYTE_ARRAYS = {INITIAL_BYTE_ARRAY, {}, {1}, {19, 26, 127, -128}};

  public static final byte[][] BYTE_ARRAYS_WITH_NULL =
      (byte[][]) ArrayUtils.addAll(BYTE_ARRAYS, (byte[]) null);

  private final UserRepository userRepository;

  private final PasswordAuthRepository passwordAuthRepository;

  private final ConfigurationRepository configurationRepository;

  private final ClassRepository classRepository;

  private final ModuleRepository moduleRepository;

  private final CsrfAttackRepository csrfAttackRepository;

  private final SubmissionRepository submissionRepository;

  private final CorrectionRepository correctionRepository;

  private final ModulePointRepository modulePointRepository;

  private final UserAuthRepository userAuthRepository;

  public Mono<Void> deleteAll() {
    // Deleting data must be done in the right order due to db constraints
    return
    // Delete all score corrections
    correctionRepository
        .deleteAll()
        // Delete all module scoring rules
        .then(csrfAttackRepository.deleteAll())
        // Delete all module scoring rules
        .then(modulePointRepository.deleteAll())
        // Delete all submissions
        .then(submissionRepository.deleteAll())
        // Delete all classes
        .then(classRepository.deleteAll())
        // Delete all modules
        .then(moduleRepository.deleteAll())
        // Delete all configuration
        .then(configurationRepository.deleteAll())
        // Delete all password auth data
        .then(passwordAuthRepository.deleteAll())
        // Delete all user auth data
        .then(userAuthRepository.deleteAll())
        // Delete all users
        .then(userRepository.deleteAll());
  }
}
