#--coding:utf-8--

class Fish:
    hungry = True
    def eat(self,food):
        if food is not None:
            self.hungry=False
 
class User:
    def __init__(self,name):
        self.name=name
 
f=Fish()
Fish.eat(f,None)
print(f.hungry)
# 下面两种方法调用等价
Fish.eat(f,"earthworm")
f.eat("earthworm")
print(f.hungry)
u=User('zhangsan')
print(u.name)

