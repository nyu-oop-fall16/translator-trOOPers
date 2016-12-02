package inputs.test017;

class A {
    A self;

    public A(int x) {
        self = this;
    }

    public A self() {
        return self;
    }
}

public class Test017 {
    public static void main(String[] args) {
        A a = new A(5);
        System.out.println(a.self().toString());
    }
}
//main.cpp
//#include <iostream>
//#include "output.h"
//        #include "java_lang.h"
//
//        using namespace std;
//        using namespace java::lang;
//
//        int main(){
//        A a = new A(5);
//        std::cout << a->self->_vptr->toString(a->self) << std::endl;
//        return 0;
//        }

//output.cpp
//
//#include "java_labg.h"
//        #include "output.h"
//        namespace inputs{
//        namespace test017{
//        __A::__A(int x):__vptr(&_vtable),self(this)
//        {}
//
//        A __A::self(A __this){
//        return self;
//        }
//
//
//        Class __A::__class(){
//static Class k = new __Class(__rt::literal("inputs.test17.A"), (Class) __Object::__class());
//        return k;
//        }
//
//        __A_VT __A::__vtable;
//
//        }
//        }
