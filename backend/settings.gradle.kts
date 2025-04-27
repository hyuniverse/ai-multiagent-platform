rootProject.name = "MultiAgentPlatform"

include("global-utils")
include("app")
include("config")

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
include("core:infra:observability")
findProject(":core:infra:observability")?.name = "observability"
include("core:infra:resilience")
findProject(":core:infra:resilience")?.name = "resilience"
