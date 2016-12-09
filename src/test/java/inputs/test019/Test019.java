package inputs.test019;

public class Test019 {
    static int x;

    public static void main(String[] args) {
        int x;
        x = 3;
        System.out.println(x);
    }
}

/* main.cpp
#include <iostream>
#include "output.h"
#include "java_lang.h"

using namespace inputs::test019;
using namespace java::lang;

int main(){
    int32_t x;
    x = 3;
    cout << A::x << endl;
}
*/

/* output.cpp
#include "output.h"
#include "java_lang.h"

using namespace java::lang;

namespace inputs{
    namespace test0019{
        A::__A() : __vptr(&__vtable);

        Class __A::__class(){
            static Class k=new __Class(__rt::literal("java.lang.A"),(Class)__Object::__class());
            return k;
        }

        __A_VT __A:: __vtable;
    }
}
*/
