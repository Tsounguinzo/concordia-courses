<script lang="ts">
    import {courseIdToUrlParam} from "$lib/utils";

    export let text: string = "";
    export let exclude: string = "";

    function processText(text: string) {
        // Pattern: 3 or 4 letters, optional space, 1 to 4 digits, optional space, optionally 1 or 2 letters at the end
        const courseCodeRegex = /\b[A-Z]{3,4} ?\d{1,4}[A-Z]{0,2}\b/g;

        // Split the text only when a full stop is followed by an uppercase letter
        const bulletPoints = text.split(/(?<=\.)\s*(?=[A-Z])/)
            .filter(s => !!s
                && !(exclude && s.replace(/\s+/g, '').includes(exclude?.replace(/\s+/g, '').replace('.', '')))
            )
            .map(point => point.trim());

        return bulletPoints
            .map(point => {
            // Replace course codes with links
            const processedPoint = point.replace(courseCodeRegex, match => {
                return `<a class="underline font-medium transition hover:text-primary-600" href="/course/${courseIdToUrlParam(match.replace(/\s+/g, '').toUpperCase())}">${match}</a>`;
            });
            return processedPoint;
        });
    }

    const processedText = processText(text);
</script>

<style>
    ul {
        list-style-type: disc;
        padding-left: 20px;
    }
    li {
        margin-bottom: 10px;
    }
</style>

<ul>
    {#each processedText as point}
        <li>{@html point}</li>
    {/each}
</ul>