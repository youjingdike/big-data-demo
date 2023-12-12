#--coding:utf-8--


def helloworld():
    print 'helloworld'
    from py_09_import_error_1 import helloworld_0
    helloworld_0()
    
if __name__ == '__main__':
    helloworld()


