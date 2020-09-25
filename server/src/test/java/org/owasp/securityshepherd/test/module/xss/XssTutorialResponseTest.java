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
package org.owasp.securityshepherd.test.module.xss;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.module.xss.XssTutorialResponse;
import org.owasp.securityshepherd.module.xss.XssTutorialResponse.XssTutorialResponseBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;

@DisplayName("XssTutorialResponse unit test")
class XssTutorialResponseTest {
  @Test
  void build_NullResult_ThrowsNullPointerException() {
    final XssTutorialResponseBuilder xssTutorialResponseBuilder =
        XssTutorialResponse.builder().alert("xss");
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> xssTutorialResponseBuilder.build())
        .withMessage("result is marked non-null but is null");
  }

  @Test
  void buildAlert_ValidAlert_Builds() {
    final XssTutorialResponseBuilder xssTutorialResponseBuilder = XssTutorialResponse.builder();
    for (final String alert : TestUtils.STRINGS_WITH_NULL) {
      final XssTutorialResponse xssTutorialResponse =
          xssTutorialResponseBuilder.result("result").alert(alert).build();
      assertThat(xssTutorialResponse.getAlert()).isEqualTo(alert);
    }
  }

  @Test
  void builderToString_ValidData_AsExpected() {
    final XssTutorialResponseBuilder testXssTutorialResponseBuilder =
        XssTutorialResponse.builder().result("TestXssTutorialResponse").alert("xss");
    assertThat(testXssTutorialResponseBuilder)
        .hasToString(
            "XssTutorialResponse.XssTutorialResponseBuilder(result=TestXssTutorialResponse, alert=xss)");
  }

  @Test
  void buildResult_NullResult_ThrowsNullPointerException() {
    final XssTutorialResponseBuilder xssTutorialResponseBuilder = XssTutorialResponse.builder();
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> xssTutorialResponseBuilder.result(null))
        .withMessage("result is marked non-null but is null");
  }

  @Test
  void buildResult_ValidResult_Builds() {
    final XssTutorialResponseBuilder xssTutorialResponseBuilder = XssTutorialResponse.builder();
    for (final String result : TestUtils.STRINGS) {
      final XssTutorialResponse xssTutorialResponse =
          xssTutorialResponseBuilder.result(result).build();
      assertThat(xssTutorialResponse.getResult()).isEqualTo(result);
    }
  }

  @Test
  void equals_EqualsVerifier_AsExpected() {
    EqualsVerifier.forClass(XssTutorialResponse.class)
        .withIgnoredAnnotations(NonNull.class)
        .verify();
  }

  @Test
  void toString_ValidData_AsExpected() {
    final XssTutorialResponse testXssTutorialResponse =
        XssTutorialResponse.builder().result("result is good").alert("xss warning").build();
    assertThat(testXssTutorialResponse)
        .hasToString("XssTutorialResponse(result=result is good, alert=xss warning)");
  }
}
