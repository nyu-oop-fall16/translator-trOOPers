#include "java_lang.h"
using namespace java::lang;
namespace inputs
{
namespace test007
{
struct __A;
struct __A_VT;
typedef __A* A;

struct __B;
struct __B_VT;
typedef __B* B;

struct __A
{
    __A_VT* __vptr ;

    static __A_VT __vtable ;

    String a ;

    __A();

//    static void __init(A a);

    static Class __class();
};

struct __A_VT
{
    Class (*getClass)(A);
    Class isa;
    int (*hashCode)(A);
    bool (*equals)(A, Object);
    String (*toString)(A);

    __A_VT()
        :
        getClass((Class(*)(A)) &__Object::getClass),
        isa(__A::__class()),
        hashCode((int(*)(A)) &__Object::hashCode),
        equals((bool(*)(A, Object)) &__Object::equals),
        toString((String(*)(A)) &__Object::toString)
    {
    }
};

struct __B
{
    __B_VT* __vptr ;

    static __B_VT __vtable ;

    String b ;

//    static void __init(B b);

    __B();

    static Class __class();
};

struct __B_VT
{
    Class (*getClass)(B);
    Class isa;
    int (*hashCode)(B);
    bool (*equals)(B, Object);
    String (*toString)(B);

    __B_VT()
        :
        getClass((Class(*)(B)) &__Object::getClass),
        isa(__B::__class()),
        hashCode((int(*)(B)) &__Object::hashCode),
        equals((bool(*)(B, Object)) &__Object::equals),
        toString((String(*)(B)) &__Object::toString)
    {
    }
};

}
}
