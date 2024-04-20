import com.capston.sumnote.member.service.NoteService
import com.capston.sumnote.note.dto.CreateNoteDto
import com.capston.sumnote.util.response.CustomApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/sum-note")
class NoteController(
    private val noteService: NoteService
) {

    @PostMapping
    fun createNote(@RequestBody dto: CreateNoteDto): ResponseEntity<CustomApiResponse<*>> {

        // 헤더에 포함된 토큰으로 이메일 값 가져오기
        val authentication = SecurityContextHolder.getContext().authentication
        val email = authentication.principal as String

        // 응답
        val response = noteService.createNote(dto, email)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}
