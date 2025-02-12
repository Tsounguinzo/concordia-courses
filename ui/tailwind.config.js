import flattenColorPalette from 'tailwindcss/lib/util/flattenColorPalette';

/** @type {import('tailwindcss').Config} */
export default {
    content: ['./src/**/*.{html,js,svelte,ts}'],
    darkMode: 'class',
    theme: {
        extend: {
            animation: {
                marquee: "marquee var(--duration) linear infinite",
                "marquee-vertical": "marquee-vertical var(--duration) linear infinite",
                gradient: "gradient 8s linear infinite",
            },
            keyframes: {
                marquee: {
                    from: { transform: "translateX(0)" },
                    to: { transform: "translateX(calc(-100% - var(--gap)))" },
                },
                "marquee-vertical": {
                    from: { transform: "translateY(0)" },
                    to: { transform: "translateY(calc(-100% - var(--gap)))" },
                },
                gradient: {
                    to: {
                        "background-position": "200% center",
                    },
                },
            },
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