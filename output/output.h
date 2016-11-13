#include "java_lang.h"
using namespace java::lang;namespace inputs {
namespace test001 {
struct __A;
struct __A_VT;
typedef __A* A;

struct __A {
 __A_VT* __vptr ;

static __A_VT __vtable ;

__A();

static String toString(A);
static Class __class();
};

struct __A_VT {
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
toString(&__A::toString) {
}
};

}
}
