/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.operaton.bpm.qa.upgrade.scenarios7110.timestamp;

import org.operaton.bpm.engine.history.HistoricJobLog;
import org.operaton.bpm.qa.upgrade.Origin;
import org.operaton.bpm.qa.upgrade.ScenarioUnderTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Nikola Koevski
 */
@ScenarioUnderTest("JobTimestampsScenario")
@Origin("7.11.0")
public class HistoricJobLogTimestampsTest extends AbstractTimestampMigrationTest {

  protected static final String PROCESS_DEFINITION_KEY = "jobTimestampsMigrationTestProcess";

  @ScenarioUnderTest("initJobTimestamps.1")
  @Test
  public void testDueDateConversion() {

    // when
    HistoricJobLog historicJobLog = historyService.createHistoricJobLogQuery()
      .processDefinitionKey(PROCESS_DEFINITION_KEY)
      .singleResult();

    // assume
    assertThat(historicJobLog, is(notNullValue()));

    // then
    assertThat(historicJobLog.getJobDueDate(), is(TIMESTAMP));
  }

  @ScenarioUnderTest("initJobTimestamps.1")
  @Test
  public void testTimestampConversion() {

    // when
    HistoricJobLog historicJobLog = historyService.createHistoricJobLogQuery()
      .processDefinitionKey(PROCESS_DEFINITION_KEY)
      .singleResult();

    // assume
    assertNotNull(historicJobLog);

    // then
    assertThat(historicJobLog.getTimestamp(), is(TIMESTAMP));
  }
}