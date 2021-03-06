/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.values.storable;

import org.junit.jupiter.api.Test;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.neo4j.values.storable.StringsLibrary.STRINGS;
import static org.neo4j.values.storable.Values.stringValue;
import static org.neo4j.values.storable.Values.utf8Value;
import static org.neo4j.values.utils.AnyValueTestUtil.assertEqual;

class UTF8StringValueTest
{
    @Test
    void shouldHandleDifferentTypesOfStrings()
    {
        for ( String string : STRINGS )
        {
            TextValue stringValue = stringValue( string );
            byte[] bytes = string.getBytes( UTF_8 );
            TextValue utf8 = utf8Value( bytes );
            assertEqual( stringValue, utf8 );
            assertThat( stringValue.length(), equalTo( utf8.length() ) );
        }
    }

    @Test
    void shouldTrimDifferentTypesOfStrings()
    {
        for ( String string : STRINGS )
        {
            TextValue stringValue = stringValue( string );
            byte[] bytes = string.getBytes( UTF_8 );
            TextValue utf8 = utf8Value( bytes );
            assertSame( stringValue.trim(), utf8.trim() );
        }
    }

    @Test
    void shouldLTrimDifferentTypesOfStrings()
    {
        for ( String string : STRINGS )
        {
            TextValue stringValue = stringValue( string );
            byte[] bytes = string.getBytes( UTF_8 );
            TextValue utf8 = utf8Value( bytes );
            assertSame( stringValue.ltrim(), utf8.ltrim() );
        }
    }

    @Test
    void trimShouldBeSameAsLtrimAndRtrim()
    {
        for ( String string : STRINGS )
        {
            TextValue utf8 = utf8Value( string.getBytes( UTF_8 ) );
            assertSame( utf8.trim(), utf8.ltrim().rtrim() );
        }
    }

    @Test
    void shouldSubstring()
    {
        String string = "ü";
        TextValue utf8 = utf8Value( string.getBytes( UTF_8 ) );
        assertThat( utf8.substring( 0, 1 ).stringValue(), equalTo( "ü" ) );
    }

    @Test
    void shouldRTrimDifferentTypesOfStrings()
    {
        for ( String string : STRINGS )
        {
            TextValue stringValue = stringValue( string );
            byte[] bytes = string.getBytes( UTF_8 );
            TextValue utf8 = utf8Value( bytes );
            assertSame( stringValue.rtrim(), utf8.rtrim() );
        }
    }

    @Test
    void shouldCompareTo()
    {
        for ( String string1 : STRINGS )
        {
            for ( String string2 : STRINGS )
            {

                int x = stringValue( string1 ).compareTo( utf8Value( string2.getBytes( UTF_8 ) ) );
                int y = utf8Value( string1.getBytes( UTF_8 ) ).compareTo( stringValue( string2 ) );
                int z = utf8Value( string1.getBytes( UTF_8 ) )
                         .compareTo( utf8Value( string2.getBytes( UTF_8 ) ) );

                assertThat( Math.signum( x ), equalTo( Math.signum( y ) ) );
                assertThat( Math.signum( x ), equalTo( Math.signum( z ) ) );
            }
        }
    }

    @Test
    void shouldReverse()
    {
        for ( String string : STRINGS )
        {
            TextValue stringValue = stringValue( string );
            byte[] bytes = string.getBytes( UTF_8 );
            TextValue utf8 = utf8Value( bytes );
            assertSame( stringValue.reverse(), utf8.reverse() );
        }
    }

    @Test
    void shouldHandleOffset()
    {
        // Given
        byte[] bytes = "abcdefg".getBytes( UTF_8 );

        // When
        TextValue textValue = utf8Value( bytes, 3, 2 );

        // Then
        assertSame( textValue, stringValue( "de" ) );
        assertThat( textValue.length(), equalTo( stringValue( "de" ).length() ) );
        assertSame( textValue.reverse(), stringValue( "ed" ) );
    }

    private void assertSame( TextValue lhs, TextValue rhs )
    {
        assertThat( format( "%s.length != %s.length", lhs, rhs ), lhs.length(),
                equalTo( rhs.length() ) );
        assertThat( format( "%s != %s", lhs, rhs ), lhs, equalTo( rhs ) );
        assertThat( format( "%s != %s", rhs, lhs ), rhs, equalTo( lhs ) );
        assertThat( format( "%s.hashCode != %s.hashCode", rhs, lhs ), lhs.hashCode(), equalTo( rhs.hashCode() ) );
        assertThat( format( "%s.hashCode64 != %s.hashCode64", rhs, lhs ),
                lhs.hashCode64(), equalTo( rhs.hashCode64() ) );
        assertThat( lhs, equalTo( rhs ) );
    }

    @Test
    void shouldHandleTooLargeStartPointInSubstring()
    {
        // Given
        TextValue value = utf8Value( "hello".getBytes( UTF_8 ) );

        // When
        TextValue substring = value.substring( 8, 5 );

        // Then
        assertThat( substring, equalTo( StringValue.EMTPY ) );
    }

    @Test
    void shouldHandleTooLargeLengthInSubstring()
    {
        // Given
        TextValue value = utf8Value( "hello".getBytes( UTF_8 ) );

        // When
        TextValue substring = value.substring( 3, 76 );

        // Then
        assertThat( substring.stringValue(), equalTo( "lo" ) );
    }

    @Test
    void shouldThrowOnNegativeStart()
    {
        // Given
        TextValue value = utf8Value( "hello".getBytes( UTF_8 ) );

        assertThrows(IndexOutOfBoundsException.class, () -> value.substring( -4, 3 ) );
    }

    @Test
    void shouldThrowOnNegativeLength()
    {
        // Given
        TextValue value = utf8Value( "hello".getBytes( UTF_8 ) );

        assertThrows( IndexOutOfBoundsException.class, () -> value.substring( 4, -3 ) );
    }
}
