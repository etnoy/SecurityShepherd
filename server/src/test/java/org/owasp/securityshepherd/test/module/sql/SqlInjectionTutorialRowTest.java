/**
 * This file is part of Security Shepherd.
 *
 * <p>Security Shepherd is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * <p>Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with Security
 * Shepherd. If not, see <http://www.gnu.org/licenses/>.
 */
package org.owasp.securityshepherd.test.module.sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorialRow;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorialRow.SqlInjectionTutorialRowBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("SqlInjectionTutorialRow unit test")
class SqlInjectionTutorialRowTest {
  @Test
  void build_NullNameCommentAndError_ThrowsNullPointerException() {
    final SqlInjectionTutorialRowBuilder sqlInjectionTutorialRowBuilder =
        SqlInjectionTutorialRow.builder();
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> sqlInjectionTutorialRowBuilder.build())
        .withMessage("%s", "Name, comment, and error can't all be null");
  }

  @Test
  void buildComment_ValidComment_Builds() {
    final SqlInjectionTutorialRowBuilder sqlInjectionTutorialRowBuilder =
        SqlInjectionTutorialRow.builder();
    for (final String comment : TestUtils.STRINGS) {
      final SqlInjectionTutorialRow sqlInjectionTutorialRow =
          sqlInjectionTutorialRowBuilder.comment(comment).build();
      assertThat(sqlInjectionTutorialRow.getComment()).isEqualTo(comment);
    }
  }

  @Test
  void buildError_ValidError_Builds() {
    final SqlInjectionTutorialRowBuilder sqlInjectionTutorialRowBuilder =
        SqlInjectionTutorialRow.builder();
    for (final String error : TestUtils.STRINGS) {
      final SqlInjectionTutorialRow sqlInjectionTutorialRow =
          sqlInjectionTutorialRowBuilder.error(error).build();
      assertThat(sqlInjectionTutorialRow.getError()).isEqualTo(error);
    }
  }

  @Test
  void builderToString_ValidData_AsExpected() {
    final SqlInjectionTutorialRowBuilder testSqlInjectionTutorialRowBuilder =
        SqlInjectionTutorialRow.builder()
            .name("TestSqlInjectionTutorialRow")
            .comment("This is a user")
            .error("no error");
    assertThat(testSqlInjectionTutorialRowBuilder)
        .hasToString(
            "SqlInjectionTutorialRow.SqlInjectionTutorialRowBuilder(name=TestSqlInjectionTutorialRow, comment=This is a user, error=no error)");
  }

  @Test
  void buildName_ValidName_Builds() {
    final SqlInjectionTutorialRowBuilder sqlInjectionTutorialRowBuilder =
        SqlInjectionTutorialRow.builder();
    for (final String name : TestUtils.STRINGS) {
      final SqlInjectionTutorialRow sqlInjectionTutorialRow =
          sqlInjectionTutorialRowBuilder.name(name).build();
      assertThat(sqlInjectionTutorialRow.getName()).isEqualTo(name);
    }
  }

  @Test
  void buildName_ValidName_BuildsSqlInjectionTutorialRow() {
    final SqlInjectionTutorialRow sqlInjectionTutorialRow =
        SqlInjectionTutorialRow.builder().name("TestSqlInjectionTutorialRow").build();
    assertThat(sqlInjectionTutorialRow.getName()).isEqualTo("TestSqlInjectionTutorialRow");
  }

  @Test
  void equals_EqualsVerifier_AsExpected() {
    EqualsVerifier.forClass(SqlInjectionTutorialRow.class).verify();
  }

  @Test
  void toString_ValidData_AsExpected() {
    final SqlInjectionTutorialRow testSqlInjectionTutorialRow =
        SqlInjectionTutorialRow.builder().name("TestSqlInjectionTutorialRow").build();
    assertThat(testSqlInjectionTutorialRow)
        .hasToString(
            "SqlInjectionTutorialRow(name=TestSqlInjectionTutorialRow, comment=null, error=null)");
  }
}
