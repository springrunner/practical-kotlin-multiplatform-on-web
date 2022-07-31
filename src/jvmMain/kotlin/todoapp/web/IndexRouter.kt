package todoapp.web

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

/**
 * 인덱스 페이지 라우터
 *
 * @author springrunner.kr@gmail.com
 */
class IndexRouter : RouterFunction<ServerResponse> {

    private val delegate = router {
        accept(MediaType.TEXT_HTML).nest {
            GET("/") {
                ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml)
            }
        }
    }
    private val indexHtml = buildString {
        appendLine("<!DOCTYPE html>")
        appendHTML(xhtmlCompatible = true).html {
            head {
                title("KMP • TodoMVC")
                meta(charset = "utf-8")
                link(href = "/webjars/todomvc-common/1.0.5/base.css", rel = "stylesheet")
                link(href = "/webjars/todomvc-app-css/2.4.1/index.css", rel = "stylesheet")
            }
            body {
                // TODO 다음 HTML 태그를 작성하세요
                // <div id="root"></div>
                // <script src="/main.js"></script>
            }
        }
    }

    override fun route(request: ServerRequest) = delegate.route(request)
}
