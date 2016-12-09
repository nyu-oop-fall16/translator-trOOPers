package inputs.test015;

class A {
    public A some;

    public void printOther(A other) {
        System.out.println(other.toString());
    }
}

class B extends A {
    public void printOther(A other) {
        System.out.println(other.toString());
    }

    public String toString() {
        return some.toString();
    }
}

public class Test015 {
    public static void main(String[] args) {
        A a = new A();
        B other = new B();
        other.some = a;
        a.printOther(other);
    }
}

/* main.cpp
#include <iostream>
#include "output.h"
#include "java_lang.h"

using namespace inputs::test015;
using namespace java::lang;
using namespace std;

int main(){
    A a = new __A();
    B other = new __B();
    other->some = a;
    a->__vptr->printOther(other);
}

*/

/* output.cpp
#include "output.h"
#include "java_lang.h"

using namespace java::lang;

namespace inputs{
    namespace test0015{
        A::__A() : __vptr(&__vtable), some(null);

        void A::printOther(A __this, A other){
            cout << __this->__vptr->toString(other) << endl;
        }

        Class __A::__class(){
            static Class k=new __Class(__rt::literal("java.lang.A"),(Class)__Object::__class());
            return k;
        }

        __A_VT __A:: __vtable;


        B::__B() : __vptr(&__vtable), some(null);

        void B::printOther(B __this, A other){
            cout << __this->__vptr->toString(other) << endl;
        }

        String B::toString(B __this){
            return __this->some->__vptr->toString(__this);
        }

        Class __B::__class(){
            static Class k=new __Class(__rt::literal("java.lang.B"),(Class)__A::__class());
            return k;
        }

        __B_VT __B:: __vtable;
    }
}

*/
