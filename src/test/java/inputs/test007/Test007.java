package inputs.test007;

class A {
    String a;

    public A() {
        a = "A";
    }
}

class B extends A {
    String b;

    public B() {
        b = "B";
    }
}


public class Test007 {
    public static void main(String[] args) {
        B b = new B();
        System.out.println(b.a);
        System.out.println(b.b);
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
//        B b = new __B();
//        std::cout << b->__vptr->a << std::endl;
//        std::cout << b->__vptr->b << std::endl;
//        return 0;
//        }



//output.cpp
//#include "java_labg.h"
//        #include "output.h"
//        namespace inputs{
//        namespace test007{
//        __A::__A():__vptr(&_vtable),a(new __String("A")
//        {}
//
//        Class __A::__class(){
//static Class k = new __Class(__rt::literal("inputs.test07.A"), (Class) __Object::__class());
//        return k;
//        }
//
//        __A_VT __A::__vtable;
//
//        __B::__B():__vptr(&_vtable),b(new __String("B")
//        {}
//
//        Class __A::__class(){
//static Class k = new __Class(__rt::literal("inputs.test07.B"), (Class) __A::__class());
//        return k;
//        }
//
//        __B_VT __B::__vtable;
//
//        }
//        }

