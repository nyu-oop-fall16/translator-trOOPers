#include "output.h"
 #include "java_lang.h"
 using namespace java::lang;
 namespace inputs {
 namespace test001 {
 __A :: __A (): __vptr(&__vtable) { } String __A::toString ( A __this ) {
 return new __String ( "A" ) ; }
 Class __A::__class ( ) {
 static Class k = new __Class(__rt::literal("java.lang.A"), (Class)__Object::__class()); return k ; }
 __A_VT __A:: __vtable; }
 }
