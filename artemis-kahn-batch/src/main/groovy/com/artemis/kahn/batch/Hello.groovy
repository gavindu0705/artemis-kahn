package com.artemis.kahn.batch

class Hello {
    def say(name) {
        println "hello ${name}!!!"
    }

    public static void main(String[] args) {
        Hello hello = new Hello();
        hello.say("duxiaoyu")
    }
}
