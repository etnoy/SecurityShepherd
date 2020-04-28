/**
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

package org.owasp.securityshepherd.module.sqlinjection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorialRow.SqlInjectionTutorialRowBuilder;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("SqlInjectionTutorialRow unit test")
public class SqlInjectionTutorialRowTest {

  @Test
  public void build_NoArguments_ThrowsNullPointerException() {
    final SqlInjectionTutorialRowBuilder sqlInjectionTutorialRowBuilder =
        SqlInjectionTutorialRow.builder();
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> sqlInjectionTutorialRowBuilder.build())
        .withMessage("%s", "Name, comment, and error can't all be null");
  }

  @Test
  public void buildName_ValidName_BuildsSqlInjectionTutorialRow() {
    final SqlInjectionTutorialRow sqlInjectionTutorialRow =
        SqlInjectionTutorialRow.builder().name("TestSqlInjectionTutorialRow").build();

    assertThat(sqlInjectionTutorialRow.getName()).isEqualTo("TestSqlInjectionTutorialRow");
  }

  @Test
  public void equals_EqualsVerifier_AsExpected() {
    EqualsVerifier.forClass(SqlInjectionTutorialRow.class).verify();
  }

  @Test
  public void toString_ValidData_AsExpected() {
    final SqlInjectionTutorialRow testSqlInjectionTutorialRow =
        SqlInjectionTutorialRow.builder().name("TestSqlInjectionTutorialRow").build();

    assertThat(testSqlInjectionTutorialRow.toString()).isEqualTo(
        "SqlInjectionTutorialRow(name=TestSqlInjectionTutorialRow, comment=null, error=null)");
  }
}
