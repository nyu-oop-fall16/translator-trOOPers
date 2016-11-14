#include "java_lang.h"
using namespace java::lang;namespace inputs {
namespace test006 {
struct __A;
struct __A_VT;
typedef __A* A;

struct __A {
 __A_VT* __vptr ;

static __A_VT __vtable ;

 String fld = "A";

__A();

static String setFld(A, String);
static String almostSetFld(A, String);
static String getFld(A);
static Class __class();
};

struct __A_VT {
String (*getFld)(A);
Class (*getClass)(A);
Class isa;
int (*hashCode)(A);
String (*almostSetFld)(A, String);
bool (*equals)(A, Object);
String (*toString)(A);
String (*setFld)(A, String);

__A_VT() 
:
getFld(&__A::getFld),
getClass((Class(*)(A)) &__Object::getClass),
isa(__A::__class()),
hashCode((int(*)(A)) &__Object::hashCode),
almostSetFld(&__A::almostSetFld),
equals((bool(*)(A, Object)) &__Object::equals),
toString((String(*)(A)) &__Object::toString),
setFld(&__A::setFld) {
}
};

}
}
