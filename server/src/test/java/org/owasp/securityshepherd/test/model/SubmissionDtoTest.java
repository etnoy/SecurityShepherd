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
package org.owasp.securityshepherd.test.model;

import static org.assertj.core.api.Assertions.assertThat;
import javax.validation.constraints.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.scoring.SubmissionDto;
import org.owasp.securityshepherd.test.util.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("SubmissionDto unit test")
public class SubmissionDtoTest {
  @Test
  void buildComment_ValidComment_Builds() {
    for (final long moduleId : TestUtils.LONGS) {
      for (final String flag : TestUtils.STRINGS) {
        final SubmissionDto submissionDto = new SubmissionDto(moduleId, flag);
        assertThat(submissionDto.getModuleId()).isEqualTo(moduleId);
        assertThat(submissionDto.getFlag()).isEqualTo(flag);
      }
    }
  }

  @Test
  void equals_EqualsVerifier_AsExpected() {
    EqualsVerifier.forClass(SubmissionDto.class).withIgnoredAnnotations(NotNull.class).verify();
  }

  @Test
  void toString_ValidData_AsExpected() {
    final SubmissionDto submissionDto = new SubmissionDto(16L, "flag");
    assertThat(submissionDto.toString()).isEqualTo("SubmissionDto(moduleId=16, flag=flag)");
  }
}
