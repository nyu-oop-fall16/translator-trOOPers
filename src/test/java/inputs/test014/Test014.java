package inputs.test014;

class A {
    A some;

    public void printOther(A other) {
        System.out.println(other.toString());
    }
}

public class Test014 {
    public static void main(String[] args) {
        A a = new A();
        A other = a.some;
        a.printOther(other); // throws NullPointerException
    }
}

/* main.cpp
#include <iostream>
#include "output.h"
#include "java_lang.h"

using namespace inputs::test014;
using namespace java::lang;
using namespace std;

int main(){
    A a = new __A();
    A other = a->some;
    a->__vptr->printOther(a, other);
}
*/

/* output.cpp
#include "output.h"
#include "java_lang.h"

using namespace java::lang;
using namespace std;

namespace inputs{
    namespace test014{
        __A::__A() : __vptr(&__vtable){}

        __A::printOther(A __this, A other){
            cout << __this->__vptr->toString(other)->data << endl;
        }

        Class __A::__class(){
            static Class k=new __Class(__rt::literal("java.lang.A"),(Class)__Object::__class());
            return k;
        }

        __A_VT __A:: __vtable;
    }
}
*/
