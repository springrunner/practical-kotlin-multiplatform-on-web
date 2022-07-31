package todoapp.ui.welcome

import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.section

val WelcomePage = FC<Props> {
    section {
        css {
            position = Position.absolute
            top = 30.pct
            transform = translate(0.px, (-50).pct)
        }
        div {
            css {
                paddingBottom = 8.px
                fontSize = 20.px
                fontWeight = FontWeight.bold
            }
            +"Hello, SpringRunner"
        }
    }
}
