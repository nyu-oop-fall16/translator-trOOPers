package inputs.test011;

class A {
    String a;

    public void setA(String x) {
        a = x;
    }

    public void printOther(A other) {
        System.out.println(other.a);
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

public class Test011 {
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


/*

#include "output.h"
#include "java_lang.h"
using namespace java::lang;

namespace inputs{
    namespace test011{
        __A::A() : __vptr(&__vtable){}

        void __A::setA(String x, A __this){
            __this->a = x;
        }

        void __A::printOther(A other, A __this){
            std::cout << other->a << std::endl;
        }

        String __A::toString(A __this){
            return __this->a
        }

        __A_VT __A:: __vtable;

        __B1::__B1() :__vptr(&__vtable),fld(new __String(),b){}

        Class __B1::__class(){
            static Class k = new __Class(__rt::literal("java.lang.B1"), (Class) __A::__class());
            return k;
        }

        __B1_VT __B1::__vtable;


        __B2::__B2() :__vptr(&__vtable),fld(new __String(),b){}

        Class __B2::__class(){
            static Class k = new __Class(__rt::literal("java.lang.B2"), (Class) __A::__class());
            return k;
        }

        __B2_VT __B2::__vtable;


        __C::__C() :__vptr(&__vtable),fld(new __String(),c){}

        Class __C::__class(){
            static Class k = new __Class(__rt::literal("java.lang.C"), (Class) __B1::__class());
            return k;
        }

        __C_VT __C::__vtable;

    }

}


 */
/*
#include <iostream>
 #include "output.h"
 #include "java_lang.h"
 using namespace std ;
 using namespace java::lang;
 using namespace inputs::test011;

int main(){
A a = new __A();
a->__vptr->setA(new__String("A"),a)
B1 b1 = new __B1();
b1->__vptr->setA(new__String("B1"),b1)
B2 b2 = new __B2();
b2->__vptr->setA(new__String("B2"),b2)
C c = new __C();
c->__vptr->setA(new__String("C"),c)
a->__vptr->printOther(a,a)
a->__vptr->printOther(b1,a)
a->__vptr->printOther(b2,a)
a->__vptr->printOther(c,a)
}


 */
