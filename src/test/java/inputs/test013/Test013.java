package inputs.test013;

class A {
    String a;

    public void setA(String x) {
        a = x;
    }

    public void printOther(A other) {
        System.out.println(other.toString());
    }
}

public class Test013 {
    public static void main(String[] args) {
        A a = new A();
        A other = a;
        other.setA("A");
        a.printOther(other);
    }
}

/*

#include "output.h"
#include "java_lang.h"
using namespace java::lang;

namespace inputs{
    namespace test013{
        __A::A() : __vptr(&__vtable),fld(new __String(), a){}

        void __A::setA(String x, A __this){
            __this->a = x;
        }

        void __A::printOther(A other, A __this){
            std::cout << other->__vptr->toString(),other) << std::endl;
        }
    }
}
 */

/*
#include <iostream>
 #include "output.h"
 #include "java_lang.h"
 using namespace std ;
 using namespace java::lang;
 using namespace inputs::test013;

using namespace inputs::test013
using namespace std
int main(){
A a = new __A();
A other = a;
other->__vptr->setA(new__String("A"),a)
a->__vptr->printOther(other,a)
}

 */
