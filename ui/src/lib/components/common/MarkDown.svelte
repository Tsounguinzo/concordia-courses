<script lang="ts">
    import {marked} from "marked";
    export let source: string;
</script>

<div class="m-auto max-w-2xl space-y-12 px-4 pt-8">
    <article class="prose dark:prose-invert max-w-none prose-code:flex prose-code:flex-col prose-img:rounded-md prose-img:shadow-md prose-img:mx-auto prose-img:max-h-[clamp(400px,50vh,500px)]">
        {@html marked(source)}
    </article>
</div>