package todoapp.web

import io.kotest.core.spec.style.FunSpec
import org.springframework.http.MediaType.TEXT_HTML
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * @author springrunner.kr@gmail.com
 */
class IndexRouterSpec : FunSpec({

    val client = WebTestClient.bindToRouterFunction(IndexRouter()).build()

    test("`/` 경로로 접근시 인덱스 페이지 보여주기") {
        client.get().uri("/")
            .accept(TEXT_HTML)
            .exchange()
            .expectAll(
                { it.expectStatus().isOk },
                { it.expectHeader().contentType(TEXT_HTML) },
                {
                    it.expectBody()
                        .xpath("//div[@id='root']").exists()
                        .xpath("//script[@src='/main.js']").exists()
                }
            )
    }
})
