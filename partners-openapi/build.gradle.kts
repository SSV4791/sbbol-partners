plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
}

tasks {
    clean {
        delete("target")
    }
}

description = "OpenApi спецификация ППРБ.Digital.Партнеры"
