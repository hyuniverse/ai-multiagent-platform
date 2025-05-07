rootProject.name = "MultiAgentPlatform"

include("app")
include("config")
include("commons")

include("api:broker-api")
findProject(":api:broker-api")?.name = "broker-api"
include("api:orchestrator-api")
findProject(":api:orchestrator-api")?.name = "orchestrator-api"
include("core:domain")
findProject(":core:domain")?.name = "domain"
include("core:infra")
findProject(":core:infra")?.name = "infra"
include("core:infra:invoker")
findProject(":core:infra:invoker")?.name = "invoker"
include("foundation:resilience")
findProject(":foundation:resilience")?.name = "resilience"
include("core:infra:broker-client")
findProject(":core:infra:broker-client")?.name = "broker-client"
include("core:contract")
findProject(":core:contract")?.name = "contract"
