package cz.fb.manaus.rest

import cz.fb.manaus.core.model.TaskExecution
import cz.fb.manaus.core.repository.TaskExecutionRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@Profile(ManausProfiles.DB)
class TaskController(
    private val taskExecutionRepository: TaskExecutionRepository
) {

    val tasks: List<TaskExecution>
        @ResponseBody
        @RequestMapping(value = ["/tasks"], method = [RequestMethod.GET])
        get() = taskExecutionRepository.list()

}
