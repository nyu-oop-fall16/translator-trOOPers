package inputs.test008;

class A {
    String a;

    public A() {
        a = "A";
        System.out.println(a);
    }
}

class B extends A {
    String b;

    public B() {
        b = "B";
        a = "B";
        System.out.println(a);
    }
}


public class Test008 {
    public static void main(String[] args) {
        B b = new B();
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
//        return 0;
//        }



//output.cpp
//#include "java_labg.h"
//#include "output.h"
//        namespace inputs{
//        namespace test007{
//        __A::__A():__vptr(&_vtable),a(new __String("A")
//        {
//        std::cout << a <<std::endl;
//        }
//
//        Class __A::__class(){
//static Class k = new __Class(__rt::literal("java.lang.A"), (Class) __Object::__class());
//        return k;
//        }
//
//        __A_VT __A::__vtable;
//
//        __B::__B():__vptr(&_vtable),b(new __String("B"),a(new __String("B")
//        {
//        std::cout << a <<std::endl;
//        }
//
//        Class __A::__class(){
//static Class k = new __Class(__rt::literal("java.lang.B"), (Class) __A::__class());
//        return k;
//        }
//
//        __B_VT __B::__vtable;
//
//        }
//        }
