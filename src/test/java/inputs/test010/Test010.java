package inputs.test010;

class A {
    String a;

    public void setA(String x) {
        a = x;
    }

    public void printOther(A other) {
        System.out.println(other.toString());
    }

    public String toString() {
        return a;
    }
}

class B1 extends A {
    String b;
}

class B2 extends A {
    String b;
}

class C extends B1 {
    String c;
}

public class Test010 {
    public static void main(String[] args) {
        A a = new A();
        a.setA("A");
        B1 b1 = new B1();
        b1.setA("B1");
        B2 b2 = new B2();
        b2.setA("B2");
        C c = new C();
        c.setA("C");
        a.printOther(a);
        a.printOther(b1);
        a.printOther(b2);
        a.printOther(c);
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
//        A a = new __A();
//        a->__vptr->setA(a, new _String("A"));
//        B1 b1 = new __B1();
//        b1->__vptr->setA(b1, new _String("B1"));
//        B2 b2 = new __B2();
//        b2->__vptr->setA(b2, new _String("B2"));
//        C c = new __C();
//        c->__vptr->setA(c, new _String("C"));
//        std::cout << a->__vptr->printOther(a) << std::endl;
//        std::cout << a->__vptr->printOther(b1) << std::endl;
//        std::cout << a->__vptr->printOther(b2) << std::endl;
//        std::cout << a->__vptr->printOther(c) << std::endl;
//        return 0;
//        }

// output.cpp
// #include "java_labg.h"
// #include "output.h"
//        namespace inputs{
//        namespace test0010{
//        __A::__A():__vptr(&_vtable)
//        {}

//        __A::??

//        Class __A::__class(){
// static Class k = new __Class(__rt::literal("inputs.test07.A"), (Class) __Object::__class());
//        return k;
//        }

//        __A_VT __A::__vtable;

//        __B::__B():__vptr(&_vtable),b(new __String("B"),a(new __String("B")
//        {
//        std::cout << a <<std::endl;
//        }

//        Class __A::__class(){
// static Class k = new __Class(__rt::literal("inputs.test07.B"), (Class) __A::__class());
//        return k;
//        }

//        __B_VT __B::__vtable;

//        }
//        }
