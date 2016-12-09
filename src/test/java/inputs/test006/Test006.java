package inputs.test006;

class A {
    private String fld = "A";

    public A() { }

    public void setFld(String f) {
        fld = f;
    }

    public void almostSetFld(String f) {
        String fld;
        fld = f;
    }

    public String getFld() {
        return fld;
    }
}

public class Test006 {
    public static void main(String[] args) {
        A a = new A();
        a.almostSetFld("B");
        System.out.println(a.getFld());
        a.setFld("B");
        System.out.println(a.getFld());
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
//        int main()
//        {
//        A a = new __A();
//        a->_vptr->almostSetFld(a,new __String("B"));
//        std::cout << a->_vptr->getFld(a) << std::endl;
//        a->_vptr->setFld(a,new __String("B"));
//        std::cout << a->_vptr->getFld(a) << std::endl;
//        return 0;
//        }



//output.cpp
//#include "java_lang.h"
//        #include "output.h"
//        namespace inputs
//        {
//        namespace test006
//        {
//        __A::__A():__vptr(&__vtable) {}
//
//        void setFld(A __this, String f){
//        __this->fld=f;
//        }
//
//        void almostSetFld(A __this, String f){
//        String fld;
//        fld=f;
//        }
//
//        String getFld(A __this){
//        return __this->fld;
//        }
//
//        Class __A::__class()
//        {
//static Class k = new __Class(__rt::literal("java.lang.A"), (Class) __Object::__class());
//        return k;
//        }
//
//        __A_VT __A::__vtable;
//
//
//
//        }
//        }
