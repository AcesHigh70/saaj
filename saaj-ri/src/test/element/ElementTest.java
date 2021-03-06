/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/**
*
* @author SAAJ RI Development Team
*/

package element;

import java.util.Iterator;

import javax.xml.soap.*;
import javax.xml.parsers.*;

import junit.framework.TestCase;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.lang.reflect.Method;


public class ElementTest extends TestCase {

    public ElementTest(String name) {
        super(name);
    }

    public void testGetAnAttribute() throws Exception {
        SOAPFactory factory = SOAPFactory.newInstance();
        SOAPElement element = factory.createElement("testElement");

        Name originalAttributeName =
            factory.createName("unqualifiedName");
        String originalAttributeValue = "aValue";
        element.addAttribute(originalAttributeName, originalAttributeValue);

        Name theAttributeName = null;
        String theAttributeValue = null;
        int count = 0;
        for (Iterator eachAttribute = element.getAllAttributes();
            eachAttribute.hasNext();
            ) {
            theAttributeName = (Name) eachAttribute.next();
            theAttributeValue = element.getAttributeValue(theAttributeName);

            ++count;
            assertEquals(1, count);
        }

        assertEquals(
            "Qualified names of attributes must match",
            originalAttributeName.getQualifiedName(),
            theAttributeName.getQualifiedName());
        assertEquals(
            "Attribute values must match",
            originalAttributeValue,
            theAttributeValue);

        /*
        for (Iterator eachAttribute = element.getAllAttributesAsQNames();
            eachAttribute.hasNext();
            ) {
            assertTrue(eachAttribute.next() instanceof QName);
        }*/
    }

    public void testGetAttributeValue() throws Exception {
        SOAPFactory factory = SOAPFactory.newInstance();
        SOAPElement element = factory.createElement("testElement");

        Name originalAttributeName =
            factory.createName("unqualifiedName");
        String originalAttributeValue = "aValue";
        element.addAttribute(originalAttributeName, originalAttributeValue);
        element.removeAttribute(originalAttributeName);

        Name theAttributeName = null;
        String theAttributeValue = null;
        String unexpectedAttributelist = "";
        int count = 0;
        for (Iterator eachAttribute = element.getAllAttributes();
            eachAttribute.hasNext();
            ) {
            theAttributeName = (Name) eachAttribute.next();
            theAttributeValue = element.getAttributeValue(theAttributeName);

            ++count;

            unexpectedAttributelist += theAttributeName.getQualifiedName()
                + " = "
                + theAttributeValue
                + "\n";
        }
        assertEquals(
            "Unexpected attributes:\n" + unexpectedAttributelist,
            0,
            count);

        theAttributeValue = element.getAttributeValue(originalAttributeName);

        assertTrue(
            "Should have been null but was: " + "\"" + theAttributeValue + "\"",
            null == theAttributeValue);
    }

    public void testAddTextNode() throws Exception {
        SOAPFactory factory = SOAPFactory.newInstance();
        SOAPElement element = factory.createElement("testElement");

        String originalText = "<txt>text</txt>";
        element.addTextNode(originalText);
        String returnedText = element.getValue();

        assertEquals(originalText, returnedText);
    }

    public void testAddChildElement() throws Exception {
        SOAPFactory factory = SOAPFactory.newInstance();
        SOAPElement element = factory.createElement("testElement");

        element.addChildElement("child");

        Iterator eachChild =
            element.getChildElements(factory.createName("child"));

        assertTrue("First element is there", eachChild.hasNext());
        SOAPElement child = (SOAPElement) eachChild.next();
        assertEquals("child", child.getTagName());
        assertFalse("Extra elements", eachChild.hasNext());
    }

    public void testAddHeaderElement() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage msg = messageFactory.createMessage();

        SOAPFactory factory = SOAPFactory.newInstance();
        SOAPElement element = factory.createElement("testElement");
        element.addChildElement("child");

        SOAPHeader header = msg.getSOAPHeader();
        header.addChildElement(element);

        Iterator eachHeader = header.getChildElements();

        assertTrue("First header is there", eachHeader.hasNext());
        SOAPHeaderElement headerElement = (SOAPHeaderElement) eachHeader.next();
        assertEquals("testElement", headerElement.getTagName());
        assertFalse("Extra headers", eachHeader.hasNext());

        Iterator eachChild = headerElement.getChildElements();
        assertTrue("First header child is there", eachChild.hasNext());
        SOAPElement child = (SOAPElement) eachChild.next();
        assertEquals("child", child.getTagName());
        assertFalse("Extra children", eachChild.hasNext());
    }

    public void testGetDetailEntries() throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage msg = mf.createMessage();
        SOAPBody body = msg.getSOAPBody();
        SOAPFault fault = body.addFault();

        Detail newDetail = fault.addDetail();
        SOAPFactory soapFact = SOAPFactory.newInstance();
        SOAPElement elem =
            soapFact.createElement("detailElem", "de", "http://foo.org");
        SOAPElement entry = newDetail.addChildElement(elem);
        assertTrue(entry instanceof DetailEntry);

        Detail detail = fault.getDetail();
        Iterator detailEntries = detail.getDetailEntries();

        while (detailEntries.hasNext()) {
            Object obj = detailEntries.next();
            assertTrue(obj instanceof DetailEntry);
        }

    }

    public void testAddFault() throws Exception {
        MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        //MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage msg = mf.createMessage();
        SOAPBody body = msg.getSOAPBody();
        SOAPFactory soapFact = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        //SOAPFactory soapFact = SOAPFactory.newInstance();
        SOAPFault fault = soapFact.createFault();
        fault = (SOAPFault)body.addChildElement(fault);
        //fault.addDetail();
        //SOAPFactory soapFact1 = SOAPFactory.newInstance();
        Detail detail = soapFact.createDetail();
        fault.addChildElement(detail); 
        //msg.writeTo(System.out);
   }


    public void testConvertHeaderElement() throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage msg = mf.createMessage();
        SOAPHeader header = msg.getSOAPHeader();

        SOAPFactory soapFact = SOAPFactory.newInstance();
        SOAPElement elem =
            soapFact.createElement("headerElem", "he", "http://foo.org");
        SOAPElement se = header.addChildElement(elem);
        assertTrue(se instanceof SOAPHeaderElement);
    }
    public void testAddBodyElement() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage msg = messageFactory.createMessage();

        SOAPFactory factory = SOAPFactory.newInstance();
        SOAPElement element = factory.createElement("testElement");
        element.addChildElement("child");

        SOAPBody body = msg.getSOAPBody();
        body.addChildElement(element);

        Iterator eachBodyElement = body.getChildElements();

        assertTrue("Body element is there", eachBodyElement.hasNext());
        SOAPBodyElement bodyElement = (SOAPBodyElement) eachBodyElement.next();
        assertEquals("testElement", bodyElement.getTagName());
        assertFalse("Extra bodies", eachBodyElement.hasNext());

        Iterator eachChild = bodyElement.getChildElements();
        assertTrue("First body child is there", eachChild.hasNext());
        SOAPElement child = (SOAPElement) eachChild.next();
        assertEquals("child", child.getTagName());
        assertFalse("Extra children", eachChild.hasNext());
    }

    public void testDetachChildElements() throws Exception {
        SOAPFactory factory = SOAPFactory.newInstance();
        SOAPElement element = factory.createElement("parent");

        element.addChildElement("one");
        element.addChildElement("two");
        element.addChildElement("three");

        SOAPElement child;
        Iterator eachChild = element.getChildElements();
        assertTrue("First child", eachChild.hasNext());
        child = (SOAPElement) eachChild.next();
        child.detachNode();
        assertTrue("Second child", eachChild.hasNext());
        child = (SOAPElement) eachChild.next();
        child.detachNode();
        assertTrue("Third child", eachChild.hasNext());
        child = (SOAPElement) eachChild.next();
        child.detachNode();
        assertFalse("Extra children", eachChild.hasNext());

        eachChild = element.getChildElements();
        assertFalse("Still has children", eachChild.hasNext());
    }

    public void testAddChildElementWithQName() throws Exception {
        SOAPFactory factory = SOAPFactory.newInstance();
        SOAPElement element = factory.createElement("testElement");

        element.addChildElement("child", "prefix", "uri");
        Iterator eachChild =
            element.getChildElements(factory.createName("child", "prefix", "uri"));

        assertTrue("First element is there", eachChild.hasNext());
        SOAPElement child = (SOAPElement) eachChild.next();
        assertEquals("prefix:child", child.getTagName());
        assertFalse("Extra elements", eachChild.hasNext());
    }

    public void testAddDetailEntry() throws Exception {
        SOAPFactory soapFactory = SOAPFactory.newInstance();
        Name name =
            soapFactory.createName(
                "BasicFaultElement",
                "ns0",
                "http://faultservice.org/types");
        Name name2 =
            soapFactory.createName(
                "AdditionalElement",
                "ns0",
                "http://faultservice.org/types");
        Detail detail = soapFactory.createDetail();
        DetailEntry entry = detail.addDetailEntry(name);

        SOAPElement child = entry.addChildElement("Project");
        entry.addChildElement("Mi", "ns0", "http://faultservice.org/types");
        entry.addChildElement("Chiamo", "ns0", "http://faultservice.org/types");
        entry.addChildElement("JAXRPC", "ns0", "http://faultservice.org/types");
        entry.addChildElement("SI", "ns0", "http://faultservice.org/types");
        entry.addChildElement(
            "Implementation",
            "ns0",
            "http://faultservice.org/types");
        child.addTextNode("Il mio nome e JAXRPC SI");
        entry = detail.addDetailEntry(name2);

        child = entry.addChildElement("Project");
        child.addTextNode("2 text");
        
        Node firstChild = detail.getFirstChild();
        assertTrue(firstChild != null);
        Node secondChild = firstChild.getNextSibling();
        assertTrue(secondChild != null);
    }
    
    public void testConvertElementWithXmlnsAttribute() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPHeader header = message.getSOAPHeader();
        
        SOAPPart document = message.getSOAPPart();
        Element element = document.createElementNS("http://bogus", "b:foo");
        element.setAttribute("xmlns", "http://bogus");
        
        header.insertBefore(element,null);
        
        Element firstChild = (Element) header.getChildElements().next();
        assertTrue(firstChild instanceof SOAPHeaderElement);
    }

    public void testCreateElementWithDomElement() throws Exception {
    
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();

        Element element = document.createElementNS("http://bogus", "b:foo");
        SOAPFactory soapFactory = SOAPFactory.newInstance();
        Element childElement = document.createElementNS("http://test", "x:child");
        element.appendChild(childElement);
        element.setAttribute("junk","true");

        Element soapElement = SOAPFactory.newInstance().createElement(element);
        assertTrue(soapElement instanceof SOAPElement);

        Element soapElementCopy = soapFactory.createElement(soapElement);
        // now copy and soapElement should be the same reference
        assertTrue(soapElementCopy == soapElement);

        NodeList nl = soapElementCopy.getChildNodes();
        assertTrue(nl.getLength() == 1);
        Element testChild = (Element)nl.item(0);
        assertTrue(testChild instanceof SOAPElement);
        assertTrue(soapElementCopy.getAttribute("junk").equals("true"));
    }

    public void testCreateElementWithDomElement2() throws Exception {
    
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();

        Element element = document.createElementNS("http://bogus", "foo");
        SOAPFactory soapFactory = SOAPFactory.newInstance();
        Element childElement = document.createElementNS("http://test", "x:child");
        element.appendChild(childElement);
        element.setAttribute("junk","true");

        Element soapElement = SOAPFactory.newInstance().createElement(element);
        assertTrue(soapElement instanceof SOAPElement);
        assertTrue(soapElement.getPrefix() == null);
    }

    public void testRemoveAttribute() throws Exception {

        // testcase for bugfix 6211152

        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPFactory soapFactory = SOAPFactory.newInstance(); 
        SOAPElement soapElement = soapFactory.createElement("foo", "b", "http://bogus");
        soapElement =
            soapElement.addAttribute(
                soapFactory.createName("junk", "c", "http://bogus1"),
                "NOTDELETED");
        soapElement = message.getSOAPBody().addChildElement(soapElement);
        soapElement.removeAttribute(soapFactory.createName("junk", "c", "http://bogus1"));
        assertNull(
            soapElement.getAttributeValue(
                soapFactory.createName("junk", "c", "http://bogus1")));
    }

   public void testGetRole() throws Exception { 


        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage message = factory.createMessage();
        Document document = message.getSOAPPart();

        SOAPElement element = (SOAPElement)document.createElementNS("http://bogus", "b:foo");
        SOAPHeader  header = message.getSOAPHeader();
        SOAPHeaderElement hdrElement = (SOAPHeaderElement)header.addChildElement(element);
        hdrElement.setActor("http://bogus");
        assertTrue(hdrElement.getRole() != null);
        assertTrue(hdrElement.getActor() != null);

        hdrElement.setRole("http://bogus1");
        assertTrue(hdrElement.getActor() != null);

   }

   public void testGetActor() throws Exception { 

        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        Document document = message.getSOAPPart();

        SOAPElement element = (SOAPElement)document.createElementNS("http://bogus", "b:foo");
        SOAPHeader  header = message.getSOAPHeader();
        SOAPHeaderElement hdrElement = (SOAPHeaderElement)header.addChildElement(element);
        hdrElement.setActor("http://bogus");
        assertTrue(hdrElement.getActor() != null);
        try {
            hdrElement.getRole();
            assertTrue(false);
        } catch (UnsupportedOperationException ex) {
        }
   }

   public void testGetSetRelay11() throws Exception { 

        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        Document document = message.getSOAPPart();

        SOAPElement element = (SOAPElement)document.createElementNS("http://bogus", "b:foo");
        SOAPHeader  header = message.getSOAPHeader();
        SOAPHeaderElement hdrElement = (SOAPHeaderElement)header.addChildElement(element);
        try {
            hdrElement.setRelay(true);
            assertTrue(false);
        } catch (UnsupportedOperationException ex) {

        }

        try {
            hdrElement.getRelay();
            assertTrue(false);
        } catch (UnsupportedOperationException ex) {
        }
   }

   public void testGetSetRelay12() throws Exception { 

        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage message = factory.createMessage();
        Document document = message.getSOAPPart();

        SOAPElement element = (SOAPElement)document.createElementNS("http://bogus", "b:foo");
        SOAPHeader  header = message.getSOAPHeader();
        SOAPHeaderElement hdrElement = (SOAPHeaderElement)header.addChildElement(element);
        hdrElement.setRelay(true);

        assertTrue(hdrElement.getRelay() == true);
   }
   private static Name createFromTagName(String tagName) {
        Class cls = null;
        try {
            cls = Thread.currentThread().getContextClassLoader().
                    loadClass("com.sun.xml.messaging.saaj.soap.name.NameImpl");
        } catch (Exception e) {
            try {
                cls = Thread.currentThread().getContextClassLoader().loadClass("com.sun.xml.internal.messaging.saaj.soap.name.NameImpl");
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (cls != null) {
            Method meth = null;
            try {
                meth = cls.getMethod("create", String.class, String.class, String.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try {
                int index = tagName.indexOf(':');
                if (index < 0) {

                    Name nm = (Name) meth.invoke(null, (Object[]) new String[]{tagName, "", ""});
                    return nm;

                } else {
                    Name nm = (Name) meth.invoke(null,(Object[]) new String[]{
                                tagName.substring(index + 1),
                                tagName.substring(0, index),
                                ""});
                    return nm;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    private static Name createName(String tagName, String uri) {
        Class cls = null;
        try {
            cls = Thread.currentThread().getContextClassLoader().
                    loadClass("com.sun.xml.messaging.saaj.soap.name.NameImpl");
        } catch (Exception e) {
            try {
                cls = Thread.currentThread().getContextClassLoader().loadClass("com.sun.xml.internal.messaging.saaj.soap.name.NameImpl");
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (cls != null) {
            Method meth = null;
            try {
                meth = cls.getMethod("create",String.class, String.class, String.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try {
                int index = tagName.indexOf(':');
                if (index < 0) {

                    Name nm = (Name) meth.invoke(null, (Object[]) new String[]{tagName, "", uri});
                    return nm;

                } else {
                    Name nm = (Name) meth.invoke(null,(Object[]) new String[]{
                                tagName.substring(index + 1),
                                tagName.substring(0, index),
                                uri});
                    return nm;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
