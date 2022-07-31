package todoapp.ui

import kotlinx.browser.window
import mui.material.Breadcrumbs
import org.w3c.dom.url.URLSearchParams
import react.FC
import react.Props
import react.create
import react.dom.html.ReactHTML
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter
import react.router.dom.Link
import todoapp.application.TodoManagerFactory
import todoapp.ui.shared.ErrorBoundaryComponents.ErrorBoundary
import todoapp.ui.shared.SnackbarProviders.SnackbarProvider
import todoapp.ui.todo.TodoPage
import todoapp.ui.welcome.WelcomePage

val App = FC<Props> {
    val (todoFind, todoRegistry, todoModification, todoCleanup) =
        TodoManagerFactory.create(URLSearchParams(window.location.search))

    ErrorBoundary {
        SnackbarProvider {
            BrowserRouter {
                Breadcrumbs {
                    Link {
                        to = "/"
                        +"todos"
                    }
                    Link {
                        to = "/welcome"
                        +"welcome"
                    }
                    separator = ReactHTML.span.create {
                        +"|"
                    }
                }
                Routes {
                    Route {
                        element = TodoPage.create {
                            find = todoFind
                            registry = todoRegistry
                            modification = todoModification
                            cleanup = todoCleanup
                        }
                        path = "/"
                        index = true
                    }
                    Route {
                        element = WelcomePage.create()
                        path = "/welcome"
                    }
                }
            }
        }
        fallback = ReactHTML.p.create {
            +"Something went wrong... 🫣"
        }
    }
}
