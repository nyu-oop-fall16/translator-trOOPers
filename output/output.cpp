#include "java_lang.h"
#include "output.h"
namespace inputs
{
namespace test007
{
__A::__A():__vptr(&__vtable),a(new __String("A")) {}

//void __A::__init(A __this){
//__this->a=new __String("A");
//}

Class __A::__class()
{
    static Class k = new __Class(__rt::literal("java.lang.A"), (Class) __Object::__class());
    return k;
}

__A_VT __A::__vtable;

__B::__B():__vptr(&__vtable),b(new __String("B"))
{
}
//void __B::__init(B __this){
//__A::__init((A) __this);
//}

Class __B::__class()
{
    static Class k = new __Class(__rt::literal("java.lang.B"), (Class) __A::__class());
    return k;
}

__B_VT __B::__vtable;

}
}