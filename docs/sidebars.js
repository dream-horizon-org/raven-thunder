/** @type {import('@docusaurus/plugin-content-docs').SidebarsConfig} */
const sidebars = {
  docs: [
    {
      type: 'category',
      label: 'Getting Started',
      collapsible: false,
      items: [
        'getting-started/overview',
        'getting-started/quickstart',
      ],
    },
    {
      type: 'category',
      label: 'API Documentation',
      items: [
        'api/scalar',
        'api/admin-contracts',
        'api/thunder-api-contracts',
      ],
    },
    {
      type: 'category',
      label: 'Operations',
      items: [
        'operations/docker',
        'operations/testing',
        'operations/ci-cd',
      ],
    },
  ],
};

module.exports = sidebars;



