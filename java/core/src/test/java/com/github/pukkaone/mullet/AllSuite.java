/*
Copyright (c) 2011, Chin Huang
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice,
   this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.github.pukkaone.mullet;

import com.github.pukkaone.mullet.html.AttributeTest;
import com.github.pukkaone.mullet.html.EscapeXmlTest;
import com.github.pukkaone.mullet.html.ForTest;
import com.github.pukkaone.mullet.html.IfTest;
import com.github.pukkaone.mullet.html.IncludeTest;
import com.github.pukkaone.mullet.html.LayoutTest;
import com.github.pukkaone.mullet.html.NoCommandTest;
import com.github.pukkaone.mullet.html.RemoveTest;
import com.github.pukkaone.mullet.html.TextTest;
import com.github.pukkaone.mullet.html.UnlessTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Runs all tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    AttributeTest.class,
    DefaultScopeTest.class,
    EscapeXmlTest.class,
    ForTest.class,
    IfTest.class,
    IncludeTest.class,
    LayoutTest.class,
    NestedScopeTest.class,
    NoCommandTest.class,
    RemoveTest.class,
    TextTest.class,
    UnlessTest.class
})
public class AllSuite {
}
