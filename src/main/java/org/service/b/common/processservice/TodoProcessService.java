package org.service.b.common.processservice;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.service.b.auth.dto.UserDto;
import org.service.b.auth.service.UserService;
import org.service.b.common.config.ServiceBProcessEnums;
import org.service.b.common.message.service.ServiceBCamundaUserService;
import org.service.b.common.message.service.MessageService;
import org.service.b.todo.dto.TodoDto;
import org.service.b.todo.service.ItemService;
import org.service.b.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named("todoProcessService")
public class TodoProcessService {

  private static final Logger logger = LoggerFactory.getLogger(TodoProcessService.class);

  private static final String TODO_TASK_NAME = "todoTaskName";

  private static final String TODO_GROUP_PREFIX = "todo";

  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  private MessageService messageService;

  @Autowired
  private ItemService itemService;

  @Autowired
  private TodoService todoService;

  @Autowired
  private FormService formService;

  @Autowired
  private IdentityService identityService;

  @Autowired
  private ServiceBCamundaUserService serviceBCamundaUserService;

  @Autowired
  private UserService userService;

  public ProcessInstance startTodo(Long todo_id, UserDto createUser) {
    String pdk = ServiceBProcessEnums.TODO_PROCESS_DEFINITION_KEY.value;
    Map variables = new HashMap();
    variables.put(ServiceBProcessEnums.ENTITY_ID.value, todo_id);
    variables.put(ServiceBProcessEnums.TODO_SIMPLE.value, false);
    ProcessInstance todoProcessInstance = runtimeService.startProcessInstanceByKey(pdk, pdk + "-" + todo_id.toString(), variables);
    List<String> stringList = new ArrayList<>();
    String theFinalGroupId = serviceBCamundaUserService.getTheCamundaGroupId(TODO_GROUP_PREFIX, todo_id);
    Group todoGroup = identityService.newGroup(theFinalGroupId);
    todoGroup.setName("Todo " + theFinalGroupId);
    todoGroup.setType(pdk);
    identityService.saveGroup(todoGroup);

    String userId = createUser.getId().toString();
    String groupId = todoGroup.getId();
    identityService.createMembership(userId, groupId);
    return todoProcessInstance;
  }

  private void setTodoTaskName(Execution execution, String todoTitle) {
    runtimeService.setVariable(execution.getId(), TODO_TASK_NAME, todoTitle);
  }

  public void setTheTodoProcess(Execution execution, Long entityId) {
    TodoDto todoDto = todoService.getTodoById(entityId);
    setTodoTaskName(execution, todoDto.getTitle());
    logger.info("The Process Service is working well God Damn Hell Yeah!");
  }

  public void startSubTodoServiceItem(Long todo_id, Long item_id) {
    Map variables = new HashMap();
    variables.put("entityId", item_id);
    variables.put("item", "item-" + item_id.toString());
    variables.put("todo", todo_id);
    runtimeService.startProcessInstanceByKey("sub-todo-service-item", "service-b-todo-" + todo_id.toString(), variables);
  }

  public String getCreateUserAsAssignee(Long todo_id) {
    TodoDto todoDto = todoService.getTodoById(todo_id);
    UserDto userDto = userService.getById(todoDto.getCreatedBy());
    return userDto.getId().toString();
  }

  public void deleteTodoItem(Long item_id) {
    // itemService.deleteItem(item_id);
  }

  public void checkIfItemsOpen(Execution execution, Long todo_id) {
    TodoDto todoDto = todoService.getTodoById(todo_id);
    if (todoDto.getItems().isEmpty()) {
      runtimeService.setVariable(execution.getId(), "itemsOpen", false);
    } else {
      runtimeService.setVariable(execution.getId(), "itemsOpen", true);
    }
  }

  public void sendSubItemDone(String messageName, String processDefinitionKey, Long todoId) {
    messageService.sendMessageToCatchEvent(messageName, processDefinitionKey, todoId);
  }

  public void checkIfTodoFinished(Long todo_id) {
    TodoDto todoDto = todoService.getTodoById(todo_id);
  }

  public void deleteTodo(Execution execution, Long todo_id) {
    TodoDto todoDto = todoService.getTodoById(todo_id);
    todoService.deleteTodo(todo_id);
    String theFinalGroupId = serviceBCamundaUserService.getTheCamundaGroupId(TODO_GROUP_PREFIX, todo_id);
    identityService.deleteGroup(theFinalGroupId);
  }

  public void finishTodo(Execution execution, Long entityId) {
    logger.info("We try to finish this TODO carefully");
  }

  public List<String> getUsers(Long todoId) {
    List<UserDto> userDtoList = todoService.getTodoUsers(todoId);
    List<String> userPerTodo = new ArrayList<>();
    for (UserDto user : userDtoList) {
      userPerTodo.add(user.getId().toString());
    }
    List<User> userList = identityService.createUserQuery().memberOfGroup(serviceBCamundaUserService.getTheCamundaGroupId(TODO_GROUP_PREFIX, todoId)).orderByUserId().asc().list();

    return userPerTodo;
  }

  public String getGroup(Long todoId) {
    return serviceBCamundaUserService.getTheCamundaGroupId("todo", todoId);
  }

}
