import flattenColorPalette from 'tailwindcss/lib/util/flattenColorPalette';

/** @type {import('tailwindcss').Config} */
export default {
    content: ['./src/**/*.{html,js,svelte,ts}'],
    darkMode: 'class',
    theme: {
        extend: {
            colors: {
                primary: {
                    DEFAULT: '#df5460',
                    50: '#fdf4f3',
                    100: '#fce7e7',
                    200: '#f8d3d4',
                    300: '#f3aeb1',
                    400: '#eb8187',
                    500: '#df5460',
                    600: '#ca3448',
                    700: '#aa263b',
                    800: '#912338',
                    900: '#7a2134',
                    950: '#440d18'
                }
            }
        },
    },
    plugins: [
        addVariablesForColors,
        require('@tailwindcss/typography')
    ],
}

function addVariablesForColors({addBase, theme}) {
    let allColors = flattenColorPalette(theme('colors'));
    let newVars = Object.fromEntries(
        Object.entries(allColors).map(([key, val]) => [`--${key}`, val])
    );

    addBase({
        ':root': newVars
    });
}