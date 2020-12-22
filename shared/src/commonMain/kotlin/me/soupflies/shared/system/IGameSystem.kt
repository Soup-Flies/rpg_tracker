package me.soupflies.shared.system

interface IGameSystem {
    val name: String

    val rules: List<IRule>
}

interface IRule