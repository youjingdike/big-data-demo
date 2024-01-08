#!/usr/bin/python
# -*- coding: UTF-8 -*-
from traitlets import HasTraits, Int, Unicode, default, observe
import getpass

print("Default values, and checking type and value...")

class Identity(HasTraits):
    username = Unicode()

    @default("username")
    def _default_username(self):
        return getpass.getuser()


iden = Identity()
print(iden.username)

print("@@@@@@@observe@@@@@@@@@@@@@")

class Foo(HasTraits):
    bar = Int()
    baz = Unicode()


# foo = Foo(bar="3")  # raises a TraitError

foo = Foo()

def func(change):
    print(change["old"])
    print(change["new"])  # as of traitlets 4.3, one should be able to
    # write print(change.new) instead



foo.observe(func, names=["bar"])
foo.bar = 1  # prints '0\n 1'
foo.baz = "abc"  # prints nothing

print("!!!!!")

class Foo1(HasTraits):
    bar = Int()
    baz = Unicode()

    @observe("bar")
    def _observe_bar(self, change):
        print(change["old"])
        print(change["new"])

foo1 = Foo1()
foo1.bar = 2

print("@@@@@@@observe@@@@@@@@@@@@@")