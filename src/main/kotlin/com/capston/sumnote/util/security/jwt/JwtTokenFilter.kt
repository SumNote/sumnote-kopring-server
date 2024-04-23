import com.capston.sumnote.util.security.jwt.JwtTokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException

class JwtTokenFilter(private val jwtTokenProvider: JwtTokenProvider) : GenericFilterBean() {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, filterChain: FilterChain) {
        val token = jwtTokenProvider.resolveToken(req as HttpServletRequest)
        if (token != null) {
            try {
                if (jwtTokenProvider.validateToken(token)) {
                    val auth = jwtTokenProvider.getAuthentication(token)
                    SecurityContextHolder.getContext().authentication = auth
                } else {
                    (res as HttpServletResponse).status = HttpServletResponse.SC_UNAUTHORIZED
                    return // 토큰이 유효하지 않으면 리턴
                }
            } catch (e: Exception) {
                SecurityContextHolder.clearContext()
                (res as HttpServletResponse).status = HttpServletResponse.SC_UNAUTHORIZED
                return // 토큰이 유효하지 않으면 리턴
            }
        }
        filterChain.doFilter(req, res)
    }
}
