/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.impl.cmd;

import static org.camunda.bpm.engine.impl.util.EnsureUtil.ensureNotNull;

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationManager;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskManager;
import org.camunda.bpm.engine.impl.util.EnsureUtil;
import org.camunda.bpm.engine.task.IdentityLinkType;


/**
 * @author Joram Barrez
 */
public abstract class AddIdentityLinkCmd implements Command<Void>, Serializable {

  private static final long serialVersionUID = 1L;

  protected String userId;

  protected String groupId;

  protected String type;

  protected String taskId;

  protected TaskEntity task;

  public AddIdentityLinkCmd(String taskId, String userId, String groupId, String type) {
    validateParams(userId, groupId, type, taskId);
    this.taskId = taskId;
    this.userId = userId;
    this.groupId = groupId;
    this.type = type;
  }

  protected void validateParams(String userId, String groupId, String type, String taskId) {
    ensureNotNull("taskId", taskId);
    ensureNotNull("type is required when adding a new task identity link", "type", type);

    // Special treatment for assignee, group cannot be used an userId may be null
    if (IdentityLinkType.ASSIGNEE.equals(type)) {
      if (groupId != null) {
        throw new ProcessEngineException("Incompatible usage: cannot use ASSIGNEE"
          + " together with a groupId");
      }
    } else {
      if (userId == null && groupId == null) {
        throw new ProcessEngineException("userId and groupId cannot both be null");
      }
    }
  }

  public Void execute(CommandContext commandContext) {

    ensureNotNull("taskId", taskId);

    final TaskManager taskManager = commandContext.getTaskManager();
    task = commandContext.runWithoutAuthentication(new Callable<TaskEntity>() {
      public TaskEntity call() throws Exception {
        return taskManager.findTaskById(taskId);
      }
    });

    EnsureUtil.ensureNotNull("Cannot find task with id " + taskId, "task", task);

    AuthorizationManager authorizationManager = commandContext.getAuthorizationManager();
    authorizationManager.checkUpdateTask(task);

    if (IdentityLinkType.ASSIGNEE.equals(type)) {
      task.setAssignee(userId);
    } else if (IdentityLinkType.OWNER.equals(type)) {
      task.setOwner(userId);
    } else {
      task.addIdentityLink(userId, groupId, type);
    }

    return null;
  }

}
