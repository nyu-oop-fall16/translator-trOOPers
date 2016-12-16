package inputs.test009;

class A {
    public A self;

    public A() {
        self = this;
    }
}

public class Test009 {
    public static void main(String[] args) {
        A a = new A();
        System.out.println(a.self.toString());
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
//        A a = new A();
//        std::cout << a->self->_vptr->toString(a->self) << std::endl;
//        return 0;
//        }


//output.cpp
//namespace inputs{
//        namespace test007{
//        __A::__A():__vptr(&__vtable),self(this)
//        {
//        }
//
//        Class __A::__class(){
//static Class k=new __Class(__rt::literal("java.lang.A"),(Class)__Object::__class());
//        return k;
//        }
//
//        __A_VT __A::__vtable;
//        }
//}